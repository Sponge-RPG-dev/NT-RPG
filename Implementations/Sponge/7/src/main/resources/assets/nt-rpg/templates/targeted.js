var {{skill.id}}_executor = new (Java.extend(TargetedScriptExecutorSkill, {
    cast: function(_caster, _target, _modifier, _context) {
        {{userScript}}
    }
}));