#!/usr/bin/ruby

t = gets.to_i
t.times do |x|
    n = gets.to_i
    diners = gets.split(' ').map(&:to_i)
    ans = max = diners.max
    eating = 1
    while eating < max
        special = 0
        diners.each do |d|
            special += (d-1) / eating
        end
        ans = [ans, eating + special].min
        eating += 1
    end
    printf "Case #%d: %d\n", x + 1, ans
end