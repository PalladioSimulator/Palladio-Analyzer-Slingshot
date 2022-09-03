package org.palladiosimulator.analyzer.slingshot.ui.events;

import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;

import org.palladiosimulator.analyzer.slingshot.common.events.AbstractSystemEvent;

public class ArchitectureModelsTabBuilderStarted extends AbstractSystemEvent implements Iterable<ArchitectureModelsTabBuilderStarted.TextField> {
	
	private final Queue<TextField> queue = new PriorityQueue<>();
	
	public Builder newModelDefinition() {
		return new Builder();
	}
	
	@Override
	public Iterator<TextField> iterator() {
		return queue.iterator();
	}
	
	public final class Builder {
		private String label;
		private String promptTitle;
		private Class<?> modelClass;
		private String[] fileExtensions;
		private String fileName;
		private boolean optional = false;
		
		public Builder label(final String label) {
			this.label = label;
			return this;
		}
		
		public Builder promptTitle(final String title) {
			this.promptTitle = title;
			return this;
		}
		
		public Builder modelClass(final Class<?> modelClass) {
			this.modelClass = modelClass;
			return this;
		}
		
		public Builder fileExtensions(final String[] extensions) {
			this.fileExtensions = extensions;
			return this;
		}
		
		public Builder fileName(final String fileName) {
			this.fileName = fileName;
			return this;
		}
		
		public Builder optional(final boolean optional) {
			this.optional = optional;
			return this;
		}
		
		public void build() {
			if (this.promptTitle == null) {
				this.promptTitle = "Select " + this.label;
			}
			if (this.fileExtensions == null) {
				this.fileExtensions = new String[] { "*." + this.fileName };
			}
			if (this.optional) {
				this.label = this.label + " (Optional)";
			}
			
			queue.add(new TextField(this));
		}
	}
	
	public static final class TextField implements Comparable<TextField> {
		
		private final String label;
		private final String promptTitle;
		private final Class<?> modelClass;
		private final String[] fileExtensions;
		private final String fileName;
		private final boolean optional;
		
		private TextField(final Builder builder) {
			this.label = builder.label;
			this.promptTitle = builder.promptTitle;
			this.modelClass = builder.modelClass;
			this.fileExtensions = builder.fileExtensions;
			this.fileName = builder.fileName;
			this.optional = builder.optional;
		}
		
		public String getLabel() {
			return label;
		}

		public String getPromptTitle() {
			return promptTitle;
		}

		public Class<?> getModelClass() {
			return modelClass;
		}

		public String[] getFileExtensions() {
			return fileExtensions;
		}

		public String getFileName() {
			return fileName;
		}

		public boolean isOptional() {
			return optional;
		}

		@Override
		public int compareTo(TextField o) {
			if (this.optional == o.optional) {
				return this.label.compareTo(o.label);
			}
			return Boolean.compare(o.optional, optional);
		}
		
	}
}
