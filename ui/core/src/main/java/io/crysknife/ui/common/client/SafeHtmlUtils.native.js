const jre = goog.require('jre');

/** @define {string} */
const crysknifeTrustedtypePolicyName =
    goog.define('crysknife.trustedtype.policy.name', 'crysknife');
jre.addSystemPropertyFromGoogDefine(
    'crysknife.trustedtype.policy.name', crysknifeTrustedtypePolicyName);
