let modules = {}

for (var k in toLoad) {
    var w = toLoad[k];
    print(w);
    load(w)
}

function(){
    return modules
}
