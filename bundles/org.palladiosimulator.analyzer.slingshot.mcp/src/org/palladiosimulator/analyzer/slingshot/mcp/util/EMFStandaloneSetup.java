package org.palladiosimulator.analyzer.slingshot.mcp.util;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.palladiosimulator.pcm.PcmPackage;
import org.palladiosimulator.pcm.allocation.AllocationPackage;
import org.palladiosimulator.pcm.core.CorePackage;
import org.palladiosimulator.pcm.core.composition.CompositionPackage;
import org.palladiosimulator.pcm.core.entity.EntityPackage;
import org.palladiosimulator.pcm.parameter.ParameterPackage;
import org.palladiosimulator.pcm.protocol.ProtocolPackage;
import org.palladiosimulator.pcm.qosannotations.QosannotationsPackage;
import org.palladiosimulator.pcm.reliability.ReliabilityPackage;
import org.palladiosimulator.pcm.repository.RepositoryPackage;
import org.palladiosimulator.pcm.resourceenvironment.ResourceenvironmentPackage;
import org.palladiosimulator.pcm.resourcetype.ResourcetypePackage;
import org.palladiosimulator.pcm.seff.SeffPackage;
import org.palladiosimulator.pcm.seff.seff_performance.SeffPerformancePackage;
import org.palladiosimulator.pcm.seff.seff_reliability.SeffReliabilityPackage;
import org.palladiosimulator.pcm.system.SystemPackage;
import org.palladiosimulator.pcm.subsystem.SubsystemPackage;
import org.palladiosimulator.pcm.usagemodel.UsagemodelPackage;

/**
 * Sets up EMF for standalone (non-Eclipse) usage.
 * Registers all PCM packages and XMI resource factories.
 */
public final class EMFStandaloneSetup {

    private static boolean initialized = false;

    private EMFStandaloneSetup() {
        // Utility class
    }

    /**
     * Initialize EMF for standalone usage.
     * This must be called before loading any PCM models outside Eclipse.
     */
    public static synchronized void init() {
        if (initialized) {
            return;
        }

        // Register XMI resource factory for PCM file extensions
        Resource.Factory.Registry registry = Resource.Factory.Registry.INSTANCE;
        registry.getExtensionToFactoryMap().put("repository", new XMIResourceFactoryImpl());
        registry.getExtensionToFactoryMap().put("system", new XMIResourceFactoryImpl());
        registry.getExtensionToFactoryMap().put("allocation", new XMIResourceFactoryImpl());
        registry.getExtensionToFactoryMap().put("resourceenvironment", new XMIResourceFactoryImpl());
        registry.getExtensionToFactoryMap().put("usagemodel", new XMIResourceFactoryImpl());
        registry.getExtensionToFactoryMap().put("resourcetype", new XMIResourceFactoryImpl());
        registry.getExtensionToFactoryMap().put("monitorrepository", new XMIResourceFactoryImpl());
        registry.getExtensionToFactoryMap().put("measuringpoint", new XMIResourceFactoryImpl());
        registry.getExtensionToFactoryMap().put("slo", new XMIResourceFactoryImpl());
        registry.getExtensionToFactoryMap().put("spd", new XMIResourceFactoryImpl());
        registry.getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());

        // Register all PCM EPackages
        EPackage.Registry packageRegistry = EPackage.Registry.INSTANCE;

        // Main PCM package
        packageRegistry.put(PcmPackage.eNS_URI, PcmPackage.eINSTANCE);

        // Core packages
        packageRegistry.put(CorePackage.eNS_URI, CorePackage.eINSTANCE);
        packageRegistry.put(EntityPackage.eNS_URI, EntityPackage.eINSTANCE);
        packageRegistry.put(CompositionPackage.eNS_URI, CompositionPackage.eINSTANCE);

        // Repository and SEFF packages
        packageRegistry.put(RepositoryPackage.eNS_URI, RepositoryPackage.eINSTANCE);
        packageRegistry.put(SeffPackage.eNS_URI, SeffPackage.eINSTANCE);
        packageRegistry.put(SeffPerformancePackage.eNS_URI, SeffPerformancePackage.eINSTANCE);
        packageRegistry.put(SeffReliabilityPackage.eNS_URI, SeffReliabilityPackage.eINSTANCE);

        // System and allocation packages
        packageRegistry.put(SystemPackage.eNS_URI, SystemPackage.eINSTANCE);
        packageRegistry.put(AllocationPackage.eNS_URI, AllocationPackage.eINSTANCE);
        packageRegistry.put(SubsystemPackage.eNS_URI, SubsystemPackage.eINSTANCE);

        // Resource packages
        packageRegistry.put(ResourceenvironmentPackage.eNS_URI, ResourceenvironmentPackage.eINSTANCE);
        packageRegistry.put(ResourcetypePackage.eNS_URI, ResourcetypePackage.eINSTANCE);

        // Usage model package
        packageRegistry.put(UsagemodelPackage.eNS_URI, UsagemodelPackage.eINSTANCE);

        // Other packages
        packageRegistry.put(ParameterPackage.eNS_URI, ParameterPackage.eINSTANCE);
        packageRegistry.put(ProtocolPackage.eNS_URI, ProtocolPackage.eINSTANCE);
        packageRegistry.put(QosannotationsPackage.eNS_URI, QosannotationsPackage.eINSTANCE);
        packageRegistry.put(ReliabilityPackage.eNS_URI, ReliabilityPackage.eINSTANCE);

        initialized = true;
    }

    /**
     * Check if EMF has been initialized for standalone usage.
     */
    public static boolean isInitialized() {
        return initialized;
    }
}
