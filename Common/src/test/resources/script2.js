var module = {}

var test = function (args) {
    print('test')
}

function test2(args) {
    print('test 2')
}
//attempting to export with namespace so methods wont conflict
modules['test'] = test;
modules['test2'] = test2;

//export these
function() {
    return module;
}