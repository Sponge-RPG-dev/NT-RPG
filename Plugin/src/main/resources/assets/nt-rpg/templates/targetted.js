var {{skill.id}}_executor = new (Java.extend(TargettedScriptExecutorSkill, {
    cast: function(_caster, _target, _modifier, _context) {
        {{userScript}}
    }
}));