package io.kestra.core.scheduler.model;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;

import io.kestra.core.exceptions.FlowBlockedException;
import io.kestra.core.utils.Enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "Why governance rejects the trigger's flow.")
public record GovernanceBlock(
    Reason reason,
    @Schema(title = "The policies that reject the flow, when the reason is POLICY.") List<String> policyIds,
    @Schema(title = "The message reported to the user.") String message) {

    public static GovernanceBlock from(FlowBlockedException e) {
        return new GovernanceBlock(e.getReason(), e.getPolicyIds(), e.getMessage());
    }

    /**
     * The message is left out: a failed check embeds its cause, which can differ on every attempt.
     */
    public boolean isSameBlockAs(GovernanceBlock other) {
        return other != null && reason == other.reason && Objects.equals(policyIds, other.policyIds);
    }

    public enum Reason {
        UNKNOWN,
        // a policy rule rejected the flow
        POLICY,
        // the policies could not be checked, so the flow is rejected rather than run ungoverned
        CHECK_FAILED;

        @JsonCreator
        public static Reason fromString(final String value) {
            return Enums.getForNameIgnoreCase(value, Reason.class, UNKNOWN);
        }
    }
}
