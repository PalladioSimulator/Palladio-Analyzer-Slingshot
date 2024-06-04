package org.palladiosimulator.analyzer.slingshot.eventdriver.entity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.stream.Collectors;

import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.Subscribe;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors.IPostInterceptor;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors.IPreInterceptor;
import org.palladiosimulator.analyzer.slingshot.eventdriver.internal.contractchecker.EventContractChecker;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;

public abstract class AnnotatedSubscriber {

	private AnnotatedSubscriber() {

	}

	private static <T> Result<?> acceptEvent(final T event, final Object target, final Method method) throws Throwable {
		final Class<?> resultType = method.getReturnType();
		final Result<?> result;

		try {
			if (resultType.equals(void.class) || resultType.equals(Void.class)) {
				// Return type void is equivalent to Result.empty()
				result = Result.empty();
				method.invoke(target, event);
			} else {
				result = (Result<?>) method.invoke(target, event);
			}

			return result;
		} catch (final InvocationTargetException ex) {
			if (ex.getCause() != null
					&& (ex.getCause() instanceof Exception || ex.getCause() instanceof AssertionError)) {
				ex.getCause().printStackTrace();
				throw ex.getCause();
			}

			return Result.empty();
		}

	}

	public static <T> Subscriber.Builder<T> fromJavaMethod(
			final Class<T> forEvent,
			final Object target,
			final Method method,
			final Subscribe annotation,
			final IPreInterceptor preInterceptor,
			final IPostInterceptor postInterceptor) {
		return Subscriber.builder(forEvent)
						 .associatedContracts(EventContractChecker
								 .getOnEventContract(target, forEvent)
								 .map(onEvent -> SubscriberContract.fromAnnotation(onEvent))
								 .collect(Collectors.toList())) // TODO
						 .handlerType(Method.class)
						 .enclosingType(target.getClass())
						 .name(method.getName())
						 .priority(annotation.priority())
						 .reifiedClasses(annotation.reified())
						 .preInterceptor(preInterceptor)
						 .postInterceptor(postInterceptor)
						 .handler(event -> acceptEvent(event, target, method));
	}

}
