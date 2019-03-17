import 'brace';
import 'brace/ext/language_tools'

ace.define('ace/mode/simql_highlight_rules', ['require', 'exports', 'module', 'ace/lib/oop', 'ace/lib/lang', 'ace/mode/doc_comment_highlight_rules', 'ace/mode/text_highlight_rules'], function(acequire, exports, module) {
  const oop = acequire('../lib/oop');
  const lang = acequire('../lib/lang');
  const TextHighlightRules = acequire('./text_highlight_rules').TextHighlightRules;

  const SimqlHighlightRules = function() {
    function string(rule) {
      const start = rule.start;
      const escapeSeq = rule.escape;
      return {
        token: 'string.start',
        regex: start,
        next: [
          {token: 'constant.language.escape', regex: escapeSeq},
          {token: 'string.end', next: 'start', regex: start},
          {defaultToken: 'string'}
        ]
      };
    }

    this.$rules = {
      'start': [
        string({start: '"', escape: /\\[0'"bnrtZ\\%_]?/}),
        {
          token: 'keyword',
          regex: '<<|><|<=|>=|=|<>|>|<|:|\\?|@'
        }, {
          token: 'paren.lparen',
          regex: '[\\(]'
        }, {
          token: 'paren.rparen',
          regex: '[\\)]'
        }, {
          token: 'text',
          regex: '\\s+'
        }],
    };

    this.normalizeRules();
  };

  oop.inherits(SimqlHighlightRules, TextHighlightRules);

  exports.SimqlHighlightRules = SimqlHighlightRules;
});

