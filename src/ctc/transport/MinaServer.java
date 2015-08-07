package ctc.transport;

/* 
   MINA(非阻塞通讯模式)的基础架构
   (1) IoService 便是应用程序的入口，相当于下面代码中的 NioSocketAcceptor，他是 IoService 的一个扩展接口。
  IoService 接口可以用来添加多个 IoFilter，这些 IoFilter 符合责任链模式并由 IoProcessor 线程负责调用。
   (2) IoFilter 是作为 IoService 和 IoHandler 之间的桥梁.IoHandler 接口中最重要的一个方法是 messageReceived，
  这个方法的第二个参数是一个 Object 型的消息，到底谁来决定这个消息到底是什么类型呢？答案就在这个 IoFilter 中。
 在下面的例子中，添加了一个 IoFilter 是 new ProtocolCodecFilter(new TextLineCodecFactory())，这个过滤器的作用
 是将来自客户端输入的信息转换成一行行的文本后传递给 IoHandler，因此可以在 messageReceived 中直接将 msg对象强制转
 换成 String 对象。如果不提供任何过滤器的话，那么在 messageReceived 方法中的第二个参数类型就是一个 byte 的缓冲区，
 对应的类是 org.apache.mina.common.ByteBuffer
  MINA自身带有一些常用的过滤器，例如LoggingFilter（日志记录）、BlackListFilter（黑名单过滤）、 CompressionFilter
  （压缩）、SSLFilter（SSL加密）等
 (3)通过它的Encoder和Decoder，可以方便的扩展并支持各种基于Socket的网络协议，比如HTTP服务器、FTP服务器、Telnet服务器等等。
	要实现自己的编码/解码器(codec)只需要实现interface: ProtocolCodecFactory即可.MINA已经实现了几个常用的(codec factory):
	a)DemuxingProtocolCodecFactory
	A composite ProtocolCodecFactory that consists of multiple MessageEncoders and MessageDecoders. 
	ProtocolEncoder and ProtocolDecoder this factory returns demultiplex incoming messages
	and buffers to appropriate MessageEncoders and MessageDecoders.
	b)NettyCodecFactory
	 A MINA ProtocolCodecFactory that provides encoder and decoder for Netty2 Messages and MessageRecognizers
	c)ObjectSerializationCodecFactory
	 A ProtocolCodecFactory that serializes and deserializes Java objects. This	codec is very useful when 
	 you have to prototype your application rapidly without any specific codec.
	d)TextLineCodecFactory
	A ProtocolCodecFactory that performs encoding and decoding between a text line data and a Java string 
	object. This codec is useful especially when you work with a text-based protocols such as SMTP and IMAP.

 */

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.MdcInjectionFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import ctc.CTCServer;

public class MinaServer {
	private static int port = 9999;//系统默认监听端口
	private static NioSocketAcceptor acceptor;
	private static ServerHandler handler;
	
	public MinaServer(){}
		
	public void close(){
		if (handler != null)
			handler.broadServerQuitMsg();
		if(acceptor != null){
			acceptor.unbind();
			acceptor.dispose();//释放资源
		}
	}
	
	
	public void start() throws Exception {
		
		port = Integer.valueOf(CTCServer.getConfigureFile().getCtcServerPort());
		
		//创建一个非阻塞的Server端Socket 
		// This socket acceptor will handle incoming connections
	
		acceptor = new NioSocketAcceptor();//用于创建服务端监听
		//acceptor = new NioSocketAcceptor(4, Executors.newCachedThreadPool());

		//get a reference to the filter chain from the acceptor
		// 创建接收数据的过滤器  
		DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();

		//设置线程池，以支持多线程 2009-10-1
		Executor threadPool = Executors.newFixedThreadPool(100);//建立线程池
		chain.addLast("exector", new ExecutorFilter(threadPool));
		
		
		//配置一个IoService在一个新的IoSession建立时增加一个ExecutorFilter
		//chain.addLast("threadPool",new ExecutorFilter(Executors.newCachedThreadPool()));
		
		// Prepare the service configuration.
		MdcInjectionFilter mdcInjectionFilter = new MdcInjectionFilter();
		chain.addLast("mdc", mdcInjectionFilter);
		
		//协议编解码器   使用字符串编码  这个过滤器将一行一行(/r/n)的读取数据
		//chain.addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));//传递文本
		//设置编码过滤器
		chain.addLast("codec",new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));//传递对象
		
		//chain.addLast("logger", new LoggingFilter());
		
		// Bind
		handler = new ServerHandler();//接收异步消息用
		acceptor.setHandler(handler);//绑定Server端业务代码
		
		
		acceptor.setBacklog(500);//设置主服务监听端口的监听队列的最大值为100，如果当前已经有100个连接，再新的连接来将被服务器拒绝
		acceptor.setReuseAddress(true);//设置主服务监听的端口可以重用
	    //acceptor.getSessionConfig().setReuseAddress(true);//设置每一个非主监听连接的端口可以重用
		//acceptor.getSessionConfig().setTcpNoDelay(true);//设置为非延迟发送，为true则不组装成大包发送，收到东西马上发出
		//acceptor.getSessionConfig().setReceiveBufferSize(10240);//设置输入缓冲区的大小
		//acceptor.getSessionConfig().setSendBufferSize(10240);//设置输出缓冲区的大小
		//acceptor.setDefaultLocalAddress(new InetSocketAddress(port)); 
		//acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE,60);//session可允许空闲的最大秒数60  2009-10-1  
		 
            
		//Bind to the specified address.This kicks off the listening for incoming connections
		//绑定端口,启动服务器
		acceptor.bind(new InetSocketAddress(port));
		
	}


}
