package sshDemo;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;

public class SFTPMain {
    public static void main(String[] args) throws JSchException, SftpException {
        JSch jSch = new JSch();
        Session session = jSch.getSession("test", "172.25.255.22", 22);
        session.setPassword("testtest");
        session.setConfig("StrictHostKeyChecking", "no");
        session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
        session.setTimeout(30000);
        session.connect();

        Channel channel = session.openChannel("sftp");
        channel.connect();

        ChannelSftp channelSftp = (ChannelSftp) channel;
        channelSftp.put("/home/spore/device-status.db", "/tmp",new MyProgressMonitor(),ChannelSftp.OVERWRITE);
        channelSftp.quit();

        channel.disconnect();
        session.disconnect();
    }
}

class MyProgressMonitor implements SftpProgressMonitor{
    private long transfered;
    
    @Override
    public void init(int op, String src, String dest, long max) {
        System.out.println("开始传输.");
    }

    /**
     * 当每次传输了一个数据块后，调用count方法，count方法的参数为这一次传输的数据块大小
     */
    @Override
    public boolean count(long count) {
        transfered=transfered+count;
        System.out.println("已传输 "+transfered+" bytes数据");
        return true;
    }

    @Override
    public void end() {
        System.out.println("结束传输.");
    }
    
}
