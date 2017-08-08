package com.tunnel.server.core;

/**
 * 客户的通讯工具
 * 客户端
 */
public class Tunnel {

	/**
	 * 域名，唯一的
	 */
	private String host;
	
	/**
	 * 客户端名称
	 */
	private String clientName;

	public Tunnel(String host, String clientName) {
		this.host = host;
		this.clientName = clientName;
	}
	
	/**
	 * 只看host是否相等
	 */
    public boolean equals(Object obj) {
        if (obj instanceof Tunnel) {
        	Tunnel client = (Tunnel) obj;
            return (host.equals(client.host));
        }
        
        return super.equals(obj);
    }
        
    public int hashCode() {
        return host.hashCode();
    }

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
}
