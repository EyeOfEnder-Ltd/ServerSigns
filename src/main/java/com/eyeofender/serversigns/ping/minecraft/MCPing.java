package com.eyeofender.serversigns.ping.minecraft;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.eyeofender.serversigns.ping.StatusResponse;

public abstract class MCPing {

    private InetSocketAddress address;
    private int timeout = 7000;

    public InetSocketAddress getAddress() {
        return address;
    }

    public void setAddress(InetSocketAddress address) {
        this.address = address;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public abstract StatusResponse fetchData() throws IOException;

}
