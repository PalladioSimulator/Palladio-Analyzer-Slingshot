package org.palladiosimulator.analyzer.slingshot.common.events.modelchanges;

import java.util.Collections;
import java.util.List;

import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.allocation.AllocationContext;

public class AllocationChange extends ModelChange<Allocation> {

	private final List<AllocationContext> oldAllocationContexts;
	private final List<AllocationContext> newAllocationContexts;
	private final List<AllocationContext> deletedAllocationContexts;

	private double simulationTime;

    // Private constructor to enforce the use of the builder
    private AllocationChange(final Builder builder) {
        super(builder.allocation, builder.simulationTime);

		this.oldAllocationContexts = Collections.unmodifiableList(builder.oldAllocationContexts);
		this.newAllocationContexts = Collections.unmodifiableList(builder.newAllocationContexts);
		this.deletedAllocationContexts = Collections.unmodifiableList(builder.deletedAllocationContexts);
    }

    public List<AllocationContext> getNewAllocationContexts() {
        return newAllocationContexts;
    }

	public List<AllocationContext> getOldAllocationContexts() {
		return oldAllocationContexts;
	}

	public List<AllocationContext> getDeletedAllocationContexts() {
		return deletedAllocationContexts;
	}

	public static Builder builder() {
		return new Builder();
	}

	// Builder class for AllocationChange
    public static class Builder {

    	private Allocation allocation;
        private List<AllocationContext> newAllocationContexts;
		private List<AllocationContext> oldAllocationContexts;
		private List<AllocationContext> deletedAllocationContexts;
    	private double simulationTime = -1;


		public Builder allocation(final Allocation allocation) {
			this.allocation = allocation;
			return this;
		}

        public Builder newAllocationContexts(final List<AllocationContext> newAllocationContexts) {
            this.newAllocationContexts = newAllocationContexts;
            return this;
        }

		public Builder oldAllocationContexts(final List<AllocationContext> oldAllocationContexts) {
			this.oldAllocationContexts = oldAllocationContexts;
			return this;
		}

		public Builder deletedAllocationContexts(final List<AllocationContext> deletedAllocationContexts) {
			this.deletedAllocationContexts = deletedAllocationContexts;
			return this;
		}

		public Builder simulationTime(final double simulationTime) {
			this.simulationTime = simulationTime;
			return this;
		}

        public AllocationChange build() {
            return new AllocationChange(this);
        }
    }


}
