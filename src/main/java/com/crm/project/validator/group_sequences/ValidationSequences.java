package com.crm.project.validator.group_sequences;

import jakarta.validation.GroupSequence;
import jakarta.validation.groups.Default;

@GroupSequence({BlankCheck.class, Default.class})
public interface ValidationSequences {
}
