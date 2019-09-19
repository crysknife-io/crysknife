goog.provide('user');
/** @define {string} */
user.agent = goog.define('user.agent', 'unknown');

// ensure that user.agent defines are included
/** @suppress {extraRequire} */
const usertest = goog.require('user.agent');