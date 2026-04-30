# Numerica full example

# immutable and mutable variables
let start = 1;
let finish = 5;
var total = 0;
var count = 0;

# numeric list
var nums = [1, 2, 3, 4, 5];

# list indexing and list mutation
paste nums;
paste nums[2];
nums[2] = 10;
paste nums;

# built-in math functions
paste abs(-5);
paste sqrt(25);
paste power(2, 3);
paste max(10, 4);
paste min(10, 4);

# function with recursion
fn factorial(n) {
    when n == 0 {
        give 1;
    } otherwise {
        give n * factorial(n - 1);
    }
}

# function using built-ins
fn metric(x, y) {
    give power(x, 2) + power(y, 2);
}

# function returning a boolean
fn isPositive(x) {
    give x > 0;
}

# function with no explicit give -> returns none
fn showValue(x) {
    paste x;
}

paste factorial(5);
paste metric(3, 4);
paste isPositive(7);
paste isPositive(-2);

# when / otherwise with boolean logic
when isPositive(nums[0]) and not (nums[1] == 0) {
    paste 111;
} otherwise {
    paste 222;
}

# repeat loop with stop
var i = 0;
repeat true {
    when i == 3 {
        stop;
    }
    paste i;
    i = i + 1;
}

# range loop
range x from start to finish by 1 {
    total = total + x;
}
paste total;

# float range loop
range y from 0.5 to 2.0 by 0.5 {
    paste y;
}

# range loop with stop
range z from 1 to 10 by 1 {
    when z == 4 {
        stop;
    }
    paste z;
}

# use list values inside a loop
range k from 0 to 4 by 1 {
    count = count + nums[k];
}
paste count;

# none example
let result = showValue(99);
paste result;