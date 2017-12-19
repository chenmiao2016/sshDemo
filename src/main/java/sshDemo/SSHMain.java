package sshDemo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SSHMain {
    public static void main(String[] args) throws JSchException, IOException, InterruptedException {
        JSch jSch = new JSch();
        Session session = jSch.getSession("test", "172.25.255.22", 22);
        session.setPassword("testtest");
        session.setConfig("StrictHostKeyChecking", "no");
        session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
        session.connect(30000); // 超時時間

        Channel channel = session.openChannel("exec");
        ChannelExec channelExec = (ChannelExec) channel;
        channelExec.setCommand("ls /etc");
        channelExec.setErrStream(System.err);
        channelExec.setInputStream(null);
        channelExec.setOutputStream(System.out);
        InputStream in = channelExec.getInputStream();
        channelExec.connect();

        StringBuilder executeResultString = new StringBuilder();
        byte[] tmp = new byte[1024];
        while (true) {
            while (in.available() > 0) {
                int i = in.read(tmp, 0, 1024);
                if (i < 0)
                    break;
                executeResultString.append(new String(tmp, 0, i));
            }

            if (channelExec.isClosed()) {
                System.out.println("exit code:" + channelExec.getExitStatus());
                break;
            }
            
            Thread.sleep(1000);
        }

        channelExec.disconnect();
        session.disconnect();
        
        System.out.println("return values is:\n" + executeResultString);
    }
}
