package io.github.rahulrajsonu.systemdesign.network;

import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.cert.Certificate;

public class NetworkDiagnostics {
    private static final int TIMEOUT_MS = 10_000;

    private static double millis(long nanos){
        return (double)nanos/1_000_000_000;
    }

    private static long elapsed(long start){
        return System.nanoTime() - start;
    }

    static void main(String[] args) {
        String host = args.length >0 ? args[0] : "google.com";
        int port = 443;
        String path = "/";

        long totalStart = System.nanoTime();

        System.out.println("\n=============================");
        System.out.println("\nNETWORK DIAGNOSTICS: "+ host);
        System.out.println("\n=============================");
        try {
            long dnsStart =  System.nanoTime();
            InetAddress[] addresses = InetAddress.getAllByName(host);

            double dnsTime = millis(elapsed(dnsStart));

            System.out.printf("%n[DNS]      %.3f ms%n", dnsTime);
            for(InetAddress address : addresses){
                System.out.println("        IP: "+ address.getHostAddress());
            }

            InetAddress selectedAddress = addresses[0];

            Socket tcpSocket = new Socket();
            long tcpStart = System.nanoTime();
            tcpSocket.connect(
                    new InetSocketAddress(selectedAddress, port),
                    TIMEOUT_MS
            );

            double tcpTime = millis(elapsed(tcpStart));
            System.out.printf("[TCP]       %.3f ms%n", tcpTime);
            System.out.println("            Remote: " +
                    tcpSocket.getRemoteSocketAddress());

            tcpSocket.setSoTimeout(TIMEOUT_MS);
            // 3. TLS HANDSHAKE
            SSLSocketFactory factory =
                    (SSLSocketFactory) SSLSocketFactory.getDefault();

            long tlsStart = System.nanoTime();

            try (SSLSocket sslSocket = (SSLSocket)
                    factory.createSocket(tcpSocket, host, port, true)) {

                sslSocket.setSoTimeout(TIMEOUT_MS);

                SSLParameters parameters = sslSocket.getSSLParameters();
                parameters.setEndpointIdentificationAlgorithm("HTTPS");
                sslSocket.setSSLParameters(parameters);

                sslSocket.startHandshake();

                double tlsTime = millis(elapsed(tlsStart));

                System.out.printf("[TLS]       %.3f ms%n", tlsTime);
                System.out.println("            Protocol: " +
                        sslSocket.getSession().getProtocol());
                System.out.println("            Cipher: " +
                        sslSocket.getSession().getCipherSuite());

                Certificate certificate =
                        sslSocket.getSession().getPeerCertificates()[0];

                System.out.println("            Certificate: " +
                        certificate.getType());

                // 4. HTTP REQUEST
                String request =
                        "GET " + path + " HTTP/1.1\r\n" +
                                "Host: " + host + "\r\n" +
                                "User-Agent: Java-Network-Diagnostics/1.0\r\n" +
                                "Accept: */*\r\n" +
                                "Connection: close\r\n" +
                                "\r\n";

                OutputStream output = sslSocket.getOutputStream();
                InputStream input = sslSocket.getInputStream();

                long requestStart = System.nanoTime();

                output.write(request.getBytes(StandardCharsets.US_ASCII));
                output.flush();

                // 5. TIME TO FIRST BYTE / RESPONSE HEADERS
                ByteArrayOutputStream headerBuffer =
                        new ByteArrayOutputStream();

                int b;
                int matched = 0;
                byte[] terminator = {13, 10, 13, 10};

                while ((b = input.read()) != -1) {
                    headerBuffer.write(b);

                    if (b == terminator[matched]) {
                        matched++;
                        if (matched == terminator.length) {
                            break;
                        }
                    } else {
                        matched = (b == terminator[0]) ? 1 : 0;
                    }
                }

                long firstByteTime = System.nanoTime();

                if (headerBuffer.size() == 0) {
                    throw new IOException("Server returned no response");
                }

                String headers = headerBuffer.toString(
                        StandardCharsets.ISO_8859_1
                );

                String statusLine = headers.lines()
                        .findFirst()
                        .orElse("Unknown");

                System.out.printf("[TTFB]      %.3f ms%n",
                        millis(firstByteTime - requestStart));

                System.out.println("[HTTP]      " + statusLine);

                // 6. RESPONSE BODY TRANSFER
                long bytesTransferred = 0;
                byte[] buffer = new byte[8192];

                int count;
                while ((count = input.read(buffer)) != -1) {
                    bytesTransferred += count;
                }

                long transferEnd = System.nanoTime();

                System.out.printf("[XFER]      %.3f ms%n",
                        millis(transferEnd - firstByteTime));

                System.out.println("            Bytes: " +
                        bytesTransferred);

                // 7. TOTAL
                System.out.println("--------------------------------------");

                System.out.printf("[TOTAL]     %.3f ms%n",
                        millis(transferEnd - totalStart));

                System.out.println("======================================");
                System.out.println("Network diagnostics completed.");
            }

        } catch (Exception e) {
            System.err.println("\n[ERROR] " + e.getClass().getSimpleName()
                    + ": " + e.getMessage());
            System.err.printf("[ELAPSED] %.3f ms%n",
                    millis(elapsed(totalStart)));
        }
    }
}
