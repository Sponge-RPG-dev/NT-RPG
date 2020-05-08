function test(args) {
    print('test')
}

function test3(args) {
    print('test 3')
}
modules['test'] = test;
modules['test3'] = test3;

//export these
function() {
    return module;
}