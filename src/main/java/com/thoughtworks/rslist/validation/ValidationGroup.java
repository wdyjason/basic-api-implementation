package com.thoughtworks.rslist.validation;

import javax.validation.GroupSequence;

@GroupSequence({FirstValidation.class, SecondValidation.class})
public interface ValidationGroup {
}
