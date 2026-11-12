package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.repository;

import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.palladiosimulator.pcm.core.composition.AssemblyConnector;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.AssemblyInfrastructureConnector;
import org.palladiosimulator.pcm.core.composition.ProvidedDelegationConnector;
import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.RequiredRole;
import org.palladiosimulator.pcm.system.System;

/**
 * A default implementation of the {@link SystemModelRepository}. Instead of
 * directly instantiating this class, use either
 * {@link SystemModelRepository#getDefaultInstance()} or use the {@code @Inject}
 * annotation and let the module system inject this instance.
 *
 * @author Julijan Katic, Sarah Stieß
 */
public class SystemModelRepositoryImpl implements SystemModelRepository {

	private static final Logger LOGGER = Logger.getLogger(SystemModelRepositoryImpl.class);

	private System systemModel;

	@Override
	public void load(final System system) {
		this.systemModel = system;
	}

	@Override
	public Optional<AssemblyConnector> findOutgoingAssemblyConnector(final RequiredRole requiredRole,
			final AssemblyContext requiringContext) {
		final List<AssemblyConnector> connectors = this.systemModel.getConnectors__ComposedStructure().stream()
				.filter(connector -> connector instanceof AssemblyConnector)
				.map(connector -> (AssemblyConnector) connector)
				.filter(connector -> connector.getRequiredRole_AssemblyConnector().getId().equals(requiredRole.getId())
						&& connector.getRequiringAssemblyContext_AssemblyConnector().getId()
								.equals(requiringContext.getId()))
				.toList();

		if (connectors.size() > 1) {
			LOGGER.debug(String.format(
					"More than one matching connector for role %s required by %s :  %s. Selecting arbitrarily.",
					requiredRole.getId(), requiringContext.getId(), connectors.toString()));
		}

		return connectors.stream().findAny();
	}

	@Override
	public Optional<AssemblyContext> findInfrastructureAssemblyContextFromRequiredRole(
			final RequiredRole requiredRole, final AssemblyContext requiringContext) {
		return this.systemModel.getConnectors__ComposedStructure().stream()
				.filter(connector -> connector instanceof AssemblyInfrastructureConnector)
				.map(connector -> (AssemblyInfrastructureConnector) connector)
				.filter(connector -> connector
						.getRequiredRole__AssemblyInfrastructureConnector().getId().equals(requiredRole.getId())
						&& connector.getRequiringAssemblyContext__AssemblyInfrastructureConnector().getId()
								.equals(requiringContext.getId()))
				.map(AssemblyInfrastructureConnector::getProvidingAssemblyContext__AssemblyInfrastructureConnector)
				.findFirst();
	}

	@Override
	public Optional<ProvidedDelegationConnector> getConnectedProvidedDelegationConnector(
			final ProvidedRole providedRole) {

		final boolean providedRolePresent = this.systemModel.getProvidedRoles_InterfaceProvidingEntity().stream()
				.filter(systemProvidedRole -> systemProvidedRole.getId().equals(providedRole.getId()))
				.findFirst()
				.isPresent();

		if (!providedRolePresent) {
			return Optional.empty();
		}

		LOGGER.debug("Provided Role is present: " + providedRole.getEntityName());

		return this.systemModel.getConnectors__ComposedStructure().stream()
				.filter(ProvidedDelegationConnector.class::isInstance)
				.map(ProvidedDelegationConnector.class::cast)
				.filter(connector -> connector.getOuterProvidedRole_ProvidedDelegationConnector().getId()
						.equals(providedRole.getId()))
				.findFirst();
	}

}
