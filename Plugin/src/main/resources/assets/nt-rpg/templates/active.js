var {{skill.id}}_executor = new (Java.extend(ScriptExecutorSkill.static, {
    cast: function(_caster, _info, _modifier, _context) {
        {{userScript}}
    }
}));