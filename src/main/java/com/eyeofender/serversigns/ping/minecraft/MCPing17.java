package com.eyeofender.serversigns.ping.minecraft;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import com.eyeofender.serversigns.ping.StatusResponse;
import com.google.gson.Gson;

public class MCPing17 extends MCPing {

    private Gson gson = new Gson();

    private int readVarInt(DataInputStream in) throws IOException {
        int i = 0;
        int j = 0;
        while (true) {
            int k = in.readByte();
            i |= (k & 0x7F) << j++ * 7;
            if (j > 5) throw new RuntimeException("VarInt too big");
            if ((k & 0x80) != 128) break;
        }
        return i;
    }

    private void writeVarInt(DataOutputStream out, int paramInt) throws IOException {
        while (true) {
            if ((paramInt & 0xFFFFFF80) == 0) {
                out.writeByte(paramInt);
                return;
            }

            out.writeByte(paramInt & 0x7F | 0x80);
            paramInt >>>= 7;
        }
    }

    @SuppressWarnings("unused")
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
        inputStreamReader = new InputStreamReader(inputStream);

        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream handshake = new DataOutputStream(b);
        handshake.writeByte(0x00); // packet id for handshake
        writeVarInt(handshake, 4); // protocol version
        writeVarInt(handshake, getAddress().getHostName().length()); // host
                                                                     // length
        handshake.writeBytes(getAddress().getHostName()); // host string
        handshake.writeShort(getAddress().getPort()); // port
        writeVarInt(handshake, 1); // state (1 for handshake)

        writeVarInt(dataOutputStream, b.size()); // prepend size
        dataOutputStream.write(b.toByteArray()); // write handshake packet

        dataOutputStream.writeByte(0x01); // size is only 1
        dataOutputStream.writeByte(0x00); // packet id for ping
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        int size = readVarInt(dataInputStream); // size of packet
        int id = readVarInt(dataInputStream); // packet id

        if (id == -1) {
            throw new IOException("Premature end of stream.");
        }

        if (id != 0x00) { // we want a status response
            throw new IOException("Invalid packetID");
        }
        int length = readVarInt(dataInputStream); // length of json string

        if (length == -1) {
            throw new IOException("Premature end of stream.");
        }

        if (length == 0) {
            throw new IOException("Invalid string length.");
        }

        byte[] in = new byte[length];
        dataInputStream.readFully(in); // read json string
        String json = new String(in);

        long now = System.currentTimeMillis();
        dataOutputStream.writeByte(0x09); // size of packet
        dataOutputStream.writeByte(0x01); // 0x01 for ping
        dataOutputStream.writeLong(now); // time!?

        readVarInt(dataInputStream);
        id = readVarInt(dataInputStream);
        if (id == -1) {
            throw new IOException("Premature end of stream.");
        }

        if (id != 0x01) {
            throw new IOException("Invalid packetID");
        }
        long pingtime = dataInputStream.readLong(); // read response

        StatusResponse response = gson.fromJson(json, StatusResponse.class);
        response.setTime((int) (now - pingtime));

        dataOutputStream.close();
        outputStream.close();
        inputStreamReader.close();
        inputStream.close();
        socket.close();

        return response;
    }
}
