package uyun.whale.dubbo.filter;


import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.common.store.DataStore;
import org.apache.dubbo.common.utils.NetUtils;
import org.apache.dubbo.rpc.*;

import java.util.concurrent.ThreadPoolExecutor;
import static org.apache.dubbo.common.constants.CommonConstants.PROVIDER;
import static org.apache.dubbo.common.constants.CommonConstants.EXECUTOR_SERVICE_COMPONENT_KEY;

/**
 * 增加服务调用间公共参数：tenant、user、appCode等信息的传递
 * 增加线程池的可用线程数低于指定阈值时的日志输出
 * 增加调用链路跟踪参数
 * @author hesy
 */
@Activate(group = PROVIDER)
public class ProviderContextFilter implements Filter {
	private static final Logger logger = LoggerFactory.getLogger(ProviderContextFilter.class);

	// 上次打印时间
	private long lastPrintTime = System.currentTimeMillis();

	// 打印周期
	private final long duration = Long.parseLong(System.getProperty("dubbo.monitor.threadpool.duration", Integer.toString(180))) * 1000;

	// 活动线程池占比阈值
	private final double threshold = Double.parseDouble(System.getProperty("dubbo.monitor.threadpool.threshold", String.valueOf(0.9D)));

	/**
	 * 租户ID
	 */
	public static final String TENANT_ID = "tenantId";

	/**
	 * 用户ID
	 */
	public static final String USER_ID = "userId";

	/**
	 * 用户名
	 */
	public static final String USER_NAME = "userName";

	/**
	 * 语言编码
	 */
	public static final String LANGUAGE = "language";

	/**
	 * 调用跟踪
	 */
	public static final String TRACE_ID = "traceId";
	
	/**
	 * 应用编码
	 */
	public static final String APP_CODE = "appCode";

	/*
	 * 其他属性，有多个时用逗号分隔
	 */
	private static String[] additionalProps = System.getProperty("earth.dubbo.context.props", "").split(",");

	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

		// 打印线程池信息
		logThreadPoolInfoIfExceedThreshold(invoker.getUrl());

		// 传递业务参数
		transmitParams(invocation);

		return invoker.invoke(invocation);
	}

	/*
	 * 传递业务参数给服务提供者。
	 */
	private void transmitParams(Invocation invocation) {
		setAttachmentIfPresent(TENANT_ID, invocation.getAttachment(TENANT_ID));
		setAttachmentIfPresent(USER_ID, invocation.getAttachment(USER_ID));
		setAttachmentIfPresent(USER_NAME, invocation.getAttachment(USER_NAME));
		setAttachmentIfPresent(LANGUAGE, invocation.getAttachment(LANGUAGE));
		setAttachmentIfPresent(TRACE_ID, invocation.getAttachment(TRACE_ID));
		setAttachmentIfPresent(APP_CODE, invocation.getAttachment(APP_CODE));

		// 其他参数
		if (additionalProps.length > 0) {
			for (String propName : additionalProps) {
				setAttachmentIfPresent(propName, invocation.getAttachment(propName));
			}
		}
	}
	
	private void setAttachmentIfPresent(String code, String value) {
		if (value != null) {
			RpcContext.getContext().setAttachment(code, value);
		}
	}

	/*
	 * 当 dubbo 服务线程池的活动线程数超过阈值时，打印日志。
	 */
	private void logThreadPoolInfoIfExceedThreshold(URL url) {
		long times = System.currentTimeMillis() - this.lastPrintTime;
		if (times < this.duration) {
			return;
		}

		String port = Integer.toString(url.getPort());
		ThreadPoolExecutor threadPool = (ThreadPoolExecutor) ExtensionLoader.getExtensionLoader(DataStore.class).getDefaultExtension()
				.get(EXECUTOR_SERVICE_COMPONENT_KEY, port);
		if (threadPool != null && threadPool.getActiveCount() > threadPool.getMaximumPoolSize() * this.threshold) {
			StringBuilder sb = new StringBuilder();
			sb.append("dubbox.threadpool.server-handler.actives: ").append(threadPool.getActiveCount());
			sb.append(", dubbox.threadpool.server-handler.maximum: ").append(threadPool.getMaximumPoolSize());
			sb.append(", [ " + NetUtils.getLocalHost() + ":" + port + ", " + url.getParameter("application") + " ]");
			logger.warn(sb.toString());
			this.lastPrintTime = System.currentTimeMillis();
		}
	}
}
