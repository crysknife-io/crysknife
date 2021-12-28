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

import org.gwtproject.regexp.shared.RegExp;
import org.hibernate.validator.constraints.Email;

/**
 * Checks that a given string is a well-formed email address.
 * <p>
 * The specification of a valid email can be found in
 * <a href="http://www.faqs.org/rfcs/rfc2822.html">RFC 2822</a> and one can come up with a regular
 * expression matching <a href="http://www.ex-parrot.com/~pdw/Mail-RFC822-Address.html"> all valid
 * email addresses</a> as per specification. However, as this
 * <a href="http://www.regular-expressions.info/email.html">article</a> discusses it is not
 * necessarily practical to implement a 100% compliant email validator. This implementation is a
 * trade-off trying to match most email while ignoring for example emails with double quotes or
 * comments.
 * </p>
 *
 * @author Emmanuel Bernard
 * @author Hardy Ferentschik
 */
public class EmailValidator implements ConstraintValidator<Email, String> {
  private static String ATOM = "[a-z0-9!#$%&'*+/=?^_`{|}~-]";
  private static String DOMAIN = "(" + ATOM + "+(\\." + ATOM + "+)*";
  private static String IP_DOMAIN = "\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\]";

  private RegExp pattern =
      RegExp.compile("^" + ATOM + "+(\\." + ATOM + "+)*@" + DOMAIN + "|" + IP_DOMAIN + ")$", "i");

  public void initialize(Email annotation) {}

  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null || value.length() == 0) {
      return true;
    }
    return pattern.test(value);
  }
}
