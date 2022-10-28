package org.palladiosimulator.analyzer.slingshot.core.extension;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.google.inject.AbstractModule;

public final class SystemBehaviorContainer extends AbstractModule {

	private static final Logger LOGGER = LogManager.getLogger(SystemBehaviorContainer.class);
	
	private AbstractSlingshotExtension extension;
	
	public SystemBehaviorContainer(final AbstractSlingshotExtension extension) {
		this.extension = extension;
	}
	
	@Override
	public void configure() {
		this.extension.getBehaviorExtensions().stream()
			.filter(extension -> SystemBehaviorExtension.class.isAssignableFrom(extension))
			.peek(clazz -> LOGGER.debug("Following system behavior class detected: " + clazz.getName()))
			.forEach(this::bind);
	}
	
	public List<Class<? extends SystemBehaviorExtension>> getExtensions() {
		return this.extension.getBehaviorExtensions().stream()
				.filter(extension -> SystemBehaviorExtension.class.isAssignableFrom(extension))
				.map(extension -> (Class<? extends SystemBehaviorExtension>) extension)
				.collect(Collectors.toUnmodifiableList());
	}
}
