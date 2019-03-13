import 'brace';
import 'brace/ext/language_tools'

import './highlight-rule.js'

class CompletionHelper {
  constructor() {
    this.items = [
      "/dev/sda1",
      "/dev/sda2",
      "PARTLABEL=foobar_boot",
      "PARTLABEL=foobar_root"
    ];
  }
}

export const aceCompletionHelpe = new CompletionHelper()

ace.define('ace/mode/simql', ['require', 'exports', 'module', 'ace/lib/oop', 'ace/mode/text', 'ace/mode/simql_highlight_rules'],
           function(acequire, exports, module) {
             let oop = acequire('../lib/oop');
             let TextMode = acequire('../mode/text').Mode;
             let SimqlHighlightRules = acequire('./simql_highlight_rules').SimqlHighlightRules;
             // complition
             var langTools = acequire('ace/ext/language_tools');
             var myCompleter = {
               identifierRegexps: [/[^\s]+/],
               getCompletions: function(editor, session, pos, prefix, callback) {
                 console.info("myCompleter prefix:", prefix);
                 callback(
                   null,
                   aceCompletionHelpe.items.filter(entry=>{
                     return entry.includes(prefix);
                   }).map(entry=>{
                     return {
                       value: entry
                     };
                   })
                 );
               }
             }
             langTools.addCompleter(myCompleter);

             let Mode = function() {
               this.HighlightRules = SimqlHighlightRules;
               this.$behaviour = this.$defaultBehaviour;
             };
             oop.inherits(Mode, TextMode);

             (function() {
               this.lineCommentStart = ['--', '#']; // todo space
               this.blockComment = {start: '/*', end: '*/'};

               this.$id = 'ace/mode/simql';
             }).call(Mode.prototype);

             exports.Mode = Mode;
           });
