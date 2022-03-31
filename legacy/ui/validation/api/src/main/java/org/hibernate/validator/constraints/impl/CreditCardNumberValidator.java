/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat, Inc. and/or its affiliates, and
 * individual contributors by the @authors tag. See the copyright.txt in the distribution for a full
 * listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in
 * writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package org.hibernate.validator.constraints.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.hibernate.validator.constraints.CreditCardNumber;

/**
 * Check a credit card number through the Luhn algorithm.
 *
 * @author Emmanuel Bernard
 * @author Hardy Ferentschik
 */
public class CreditCardNumberValidator implements ConstraintValidator<CreditCardNumber, String> {
  private LuhnValidator luhnValidator;

  public CreditCardNumberValidator() {
    luhnValidator = new LuhnValidator(2);
  }

  public void initialize(CreditCardNumber annotation) {}

  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }
    return luhnValidator.passesLuhnTest(value);
  }
}
