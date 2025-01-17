package impl;

//import io.netty.bootstrap.Bootstrap;
//import io.netty.channel.Channel;
//import io.netty.channel.ChannelFuture;
//import io.netty.channel.ChannelInitializer;
//import io.netty.channel.ChannelOption;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.channel.socket.nio.NioSocketChannel;
//import top.paakciu.client.handler.*;
//import top.paakciu.client.listener.ClientEventListener;
//import top.paakciu.config.IMConfig;
//import codec.handler.B2MPacketCodecHandler;
//import codec.handler.PreFrameDecoder;
//
//import java.util.Date;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//
//
//public class NettyClient {
//    private static final int MAX_RETRY = IMConfig.ClientConnectionRetry;
//    private ClientEventListener clientEventListener;
//    public boolean channelisOK=false;
//    public Channel channel=null;
//    public ExecutorService executor = Executors.newFixedThreadPool(IMConfig.CLIENT_THREAD_POOL_NUM);
//
//    public Bootstrap setBootstrapHandler(Bootstrap bootstrap){
//        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
//            //连接初始化
//            @Override
//            protected void initChannel(SocketChannel socketChannel) {
//                //接口处理初始化
//                if(clientEventListener !=null){
//                    //ExecutorService executor = Executors.newFixedThreadPool(1);
//                    executor.submit(()->clientEventListener.onInitChannel());
//                }
//
//                //这里是责任链模式，然后加入逻辑处理器
//                socketChannel.pipeline()
//                        //.addLast(new clientHandler())
//                        .addLast(new ClientIdleDetectionHandler())
//                        .addLast(new PreFrameDecoder())
//                        .addLast(new B2MPacketCodecHandler())
//                        .addLast(new RegisterResponseHandler())
//                        .addLast(new LoginResponseHandler())
//                        //只在登录之后才进行处理
//                        .addLast(new MessageResponseHandler())
//                        //错误处理
//                        .addLast(new ErrorMessageHandler())
//                        //拓展单聊
//                        .addLast(new ExtraResponseHandler())
//                        //群组操作
//                        .addLast(new CreateGroupResponseHandler())
//                        .addLast(new GetGroupMembersResponseHandler())
//                        .addLast(new GroupMessageResponseHandler())
//                        .addLast(new JoinGroupResponseHandler())
//                        .addLast(new QuitGroupResponseHandler())
//                        .addLast(new GetGroupListResponseHandler())
//                        .addLast(new OffLineGroupMessageResponseHandler())
//                        .addLast(new ExtraGroupResponseHandler())
//                        .addLast(new GetInfoAndFriendResponseHandler())
//                        //心跳包的定期发送
//                        .addLast(new HeartBeatTimerHandler())
//                ;
//                //.addLast(PacketEncoder());
////                                .addLast(new ZhanBaoClientHandler());
//            }
//        });
//        return bootstrap;
//    }
//
//    public void startConnection(String host,int port)
//    {
//        //线程组
//        NioEventLoopGroup workerGroup =new NioEventLoopGroup();
//        //引导类
//        Bootstrap bootstrap =new Bootstrap();
//        //核心配置
//        bootstrap
//                //指定线程模型
//                .group(workerGroup)
//                //指定IO类型为NIO
//                .channel(NioSocketChannel.class)
//                //指定IO的处理逻辑
//                ;
//        setBootstrapHandler(bootstrap);
//        setBootstrapExtraConfig(bootstrap);
//
//        //启动连接
//        connect(bootstrap, host, port,MAX_RETRY);
//    }
//
//    //额外配置
//    public Bootstrap setBootstrapExtraConfig(Bootstrap bootstrap){
//        //额外的配置
//        bootstrap
//                // 设置TCP底层属性
//                //连接的超时时间
//                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
//                //是否开启TCP底层心跳机制
//                .option(ChannelOption.SO_KEEPALIVE, true)
//                //是否开启Nagle，即高实时性（true）减少发送次数（false）
//                .option(ChannelOption.TCP_NODELAY, true);
//        return bootstrap;
//    }
//
//    //建立连接
//    public void connect (Bootstrap bootstrap,String host,int port,int retry)
//    {
//        bootstrap
//                .connect(host, port)
//                .addListener(future -> {
//                    if (future.isSuccess()) {
//                        Channel channel = ((ChannelFuture) future).channel();
//                        // 连接成功之后，启动控制台线程
//                        channelisOK=true;
//                        this.channel=channel;
//                        //接口处理连接成功
//                        if(clientEventListener !=null){
//                            //ExecutorService executor = Executors.newFixedThreadPool(1);
//                            executor.submit(()->clientEventListener.onConnectSuccess(channel));
//                        }
//                        //startConsoleThread(channel);
//
//                    }
//                    else {
//                        //这里应该要有个随机退避算法
//                        //接口处理连接失败
//                        if(clientEventListener !=null){
//                            //ExecutorService executor = Executors.newFixedThreadPool(1);
//                            executor.submit(()->clientEventListener.onConnectFail(retry));
//                        }
//
//                        if (retry == 0) {
//                            System.err.println("重试次数已用完，放弃连接！");
//                            return;
//                        }
//                        // 第几次重连
//                        int order = (MAX_RETRY - retry) + 1;
//                        // 此次重连的间隔时间
//                        int delay = 1 << order;
//                        System.err.println(new Date() + ": 连接失败，第" + order + "次重连……");
//                        //使用计划任务来实现退避重连算法
//                        bootstrap.config().group().schedule(
//                                () -> connect(bootstrap, host, port, retry - 1)
//                                ,delay
//                                ,TimeUnit.SECONDS
//                        );
//                    }
//                });
//    }
//
//    public ClientEventListener getClientEventListener() {
//        return clientEventListener;
//    }
//
//    public void setClientEventListener(ClientEventListener clientEventListener) {
//        this.clientEventListener = clientEventListener;
//    }
//
//
//}
//






//    //连接成功后执行的方法，尽量新建线程去完成
//    private static void startConsoleThread(Channel channel) {
//        Scanner sc = new Scanner(System.in);
//
//        new Thread(() -> {
//            while (!Thread.interrupted()) {
//
//                //Attribute<Boolean> loginAttr = channel.attr(AttributeKey.exists("login")?AttributeKey.valueOf("login"):AttributeKey.newInstance("login"));
//                //如果没有登录
//                if (
//                        !AttributesHelper.hasLogin(channel)
//                        &&(AttributesHelper.getLoginState(channel)==null
//                        ||AttributesHelper.getLoginState(channel)==3
//                        ||AttributesHelper.getLoginState(channel)!=0)
//                    ) {
//                    System.out.println("输入账号: ");
//                    String username = sc.nextLine();
//                    System.out.println("输入密码: ");
//                    String password = sc.nextLine();
//
//                    LoginRequestPacket loginRequestPacket = new LoginRequestPacket(username,password);
//
//                    AttributesHelper.setLoginState(channel, AttributesHelper.LOGINSTATE.LOGINING.getValue());
//                    //ByteBuf byteBuf = PacketCodec.encode(channel.alloc().buffer(), packet);
//                    channel.writeAndFlush(loginRequestPacket);
//                }else{
//                    System.out.println("请输入【对方id 消息】");
//                    String toUserId = sc.next();
//                    String message = sc.next();
//
//                    MessageRequestPacket packet = new MessageRequestPacket();
//                    packet.setToUserId(toUserId);
//                    packet.setMessage(message);
//                    channel.writeAndFlush(packet);
//                }
//            }
//        }).start();
//    }