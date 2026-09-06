package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.repository;

import java.util.Optional;

import org.palladiosimulator.pcm.core.composition.AssemblyConnector;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.ProvidedDelegationConnector;
import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.RequiredRole;
import org.palladiosimulator.pcm.system.System;

import com.google.inject.ImplementedBy;

/**
 * A repository for system models that handles further handlers for the
 * {@code System} model. All methods that return an {@link Optional} do never
 * return {@code null}, but an empty Optional instead.
 * <p>
 * The repository allows a direct access to the model instead of using a
 * Switch-class.
 *
 * @author Julijan Katic, Sarah Stieß
 */
@ImplementedBy(SystemModelRepositoryImpl.class)
public interface SystemModelRepository {

	/**
	 * Loads the system model into the repository. This method should be called
	 * first, otherwise all other methods will not work.
	 *
	 * @param system the non-null system model to load.
	 */
	void load(System system);

	/**
	 * Get the delegation connector from a provided role. The provided role is the
	 * role attached to the whole system, not the role attached to an (inner)
	 * assembly context.
	 *
	 * @param providedRole The provided role of a system.
	 * @return The delegation connector of that provided role.
	 */
	Optional<ProvidedDelegationConnector> getConnectedProvidedDelegationConnector(ProvidedRole providedRole);

	/**
	 * Finds the {@link AssemblyConnector} that connects the required role of the
	 * requiring {@link AssemblyContext} to the provided role of another context.
	 *
	 * @param requiredRole
	 * @param requiringContext
	 * @return an {@link AssemblyConnector} that connects the required role of the
	 *         requiring context to a providing one, or an empty optional if none
	 *         exists.
	 */
	Optional<AssemblyConnector> findOutgoingAssemblyConnector(RequiredRole requiredRole,
			AssemblyContext requiringContext);

	/**
	 * Finds the connected {@link AssemblyContext} for an infrastructure call.
	 *
	 * @param requiredRole
	 * @param requiringContext
	 * @return assembly context providing for the requiring assembly context, or an
	 *         empty optional if none exists.
	 */
	Optional<AssemblyContext> findInfrastructureAssemblyContextFromRequiredRole(RequiredRole requiredRole,
			AssemblyContext requiringContext);

	/**
	 * Returns the default instance of this interface.
	 *
	 * @return an instance implementing this interface.
	 */
	static SystemModelRepository getDefaultInstance() {
		return INSTANCE;
	}

	/**
	 * A default instance implementing this interface.
	 */
	SystemModelRepository INSTANCE = new SystemModelRepositoryImpl();

}
