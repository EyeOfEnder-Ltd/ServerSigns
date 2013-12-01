package com.eyeofender.serversigns.ping;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;

import com.eyeofender.serversigns.ping.StatusResponse.Players;
import com.eyeofender.serversigns.ping.StatusResponse.Version;

public class MCPing16 extends MCPing {

    @Override
    public StatusResponse fetchData() throws IOException {
        Socket socket = new Socket();
        OutputStream outputStream;
        DataOutputStream dataOutputStream;
        InputStream inputStream;
        InputStreamReader inputStreamReader;

        socket.setSoTimeout(getTimeout());

        socket.connect(getAddress(), getTimeout());

        outputStream = socket.getOutputStream();
        dataOutputStream = new DataOutputStream(outputStream);

        inputStream = socket.getInputStream();
        inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-16BE"));

        dataOutputStream.write(new byte[] { (byte) 0xFE, (byte) 0x01, (byte) 0xFA });
        dataOutputStream.writeShort(11);
        dataOutputStream.write("MC|PingHost".getBytes("UTF-16BE"));
        dataOutputStream.writeShort((getAddress().getHostName().length() * 2) + 7);
        dataOutputStream.write(73);
        dataOutputStream.writeShort(getAddress().getHostName().length());
        dataOutputStream.write(getAddress().getHostName().getBytes("UTF-16BE"));
        dataOutputStream.writeInt(getAddress().getPort());

        int packetId = inputStream.read();

        if (packetId == -1) {
            throw new IOException("Premature end of stream.");
        }

        if (packetId != 0xFF) {
            throw new IOException("Invalid packet ID (" + packetId + ").");
        }

        int length = inputStreamReader.read();

        if (length == -1) {
            throw new IOException("Premature end of stream.");
        }

        if (length == 0) {
            throw new IOException("Invalid string length.");
        }

        char[] chars = new char[length];

        if (inputStreamReader.read(chars, 0, length) != length) {
            throw new IOException("Premature end of stream.");
        }

        String string = new String(chars);
        StatusResponse response = new StatusResponse();
        Players players = response.new Players();
        Version version = response.new Version();

        if (string.startsWith("§")) {
            String[] data = string.split("\0");

            version.setProtocol(data[1]);
            version.setName(data[2]);
            response.setDescription(data[3]);
            players.setOnline(Integer.parseInt(data[4]));
            players.setMax(Integer.parseInt(data[5]));
        } else {
            String[] data = string.split("§");

            response.setDescription(data[0]);
            players.setOnline(Integer.parseInt(data[1]));
            players.setMax(Integer.parseInt(data[2]));
        }

        response.setPlayers(players);
        response.setVersion(version);

        dataOutputStream.close();
        outputStream.close();

        inputStreamReader.close();
        inputStream.close();

        socket.close();

        return response;
    }

}
