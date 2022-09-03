package org.palladiosimulator.analyzer.slingshot.ui.tabs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.palladiosimulator.analyzer.slingshot.core.Slingshot;
import org.palladiosimulator.analyzer.slingshot.core.api.SystemDriver;
import org.palladiosimulator.analyzer.slingshot.ui.events.ArchitectureModelsTabBuilderStarted;
import org.palladiosimulator.analyzer.slingshot.ui.events.ArchitectureModelsTabBuilderStarted.TextField;

import de.uka.ipd.sdq.workflow.launchconfig.tabs.TabHelper;

public class SimulationArchitectureModelsTab extends AbstractLaunchConfigurationTab {
	
	private static final Logger LOGGER = Logger.getLogger(SimulationArchitectureModelsTab.class);
	
	private static final String NAME = "Architectural Models";
	private static final String ID = "org.palladiosimulator.analyzer.slingshot.architecturemodelstab";
	
	private Iterator<TextField> iterator;
	private final Map<TextField, Text> texts = new HashMap<>();
	
	private final ModifyListener modifyListener;
	private Composite container;
	
	public SimulationArchitectureModelsTab() {
		final SystemDriver systemDriver = Slingshot.getInstance().getSystemDriver();
		
		final ArchitectureModelsTabBuilderStarted event = new ArchitectureModelsTabBuilderStarted();
		systemDriver.postEventAndThen(event, () -> {
			System.out.println("Post Event !");
			iterator = event.iterator();
			System.out.println("POST EVENT !!!");
		});
		
		this.modifyListener = modifyEvent -> {
			setDirty(true);
			updateLaunchConfigurationDialog();
		};
	}

	@Override
	public void createControl(Composite parent) {
		this.container = new Composite(parent, SWT.NONE);
		setControl(container);
		container.setLayout(new GridLayout());
		
		this.iterator.forEachRemaining(textField -> {
			final Text text = new Text(container, SWT.SINGLE | SWT.BORDER);
			TabHelper.createFileInputSection(container, modifyListener, textField.getLabel(), textField.getFileExtensions(), text, textField.getPromptTitle(), getShell(), "");
			texts.put(textField, text);
		});
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		texts.forEach((textField, text) -> {
			try {
				text.setText(configuration.getAttribute(textField.getFileName(), ""));
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		texts.forEach((textField, text) -> {
			configuration.setAttribute(textField.getFileName(), text.getText());
		});
	}

	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public String getId() {
		return ID;
	}
	
	@Override
	public boolean isValid(ILaunchConfiguration launchConfig) {
		setErrorMessage(null);
		
		return texts.entrySet().stream()
				.filter(entry -> !entry.getKey().isOptional())
				.filter(entry -> !TabHelper.validateFilenameExtension(entry.getValue().getText(), entry.getKey().getFileExtensions()))
				.findFirst()
				.map(entry -> {
					setErrorMessage(entry.getKey().getLabel() + " is missing");
					return false;
				})
				.orElse(true);
	}
}
