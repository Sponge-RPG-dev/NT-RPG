var {{skill.id}}_executor = new (Java.extend(ScriptExecutorSkill, {
    function: cast(caster, info, modifier, context) {
        {{userScript}}
    }
}));