#!/usr/bin/ruby

t = gets.to_i
t.times do |x|
    ans = 0
    strs = gets.chomp.split(' ')
    n = strs[0].to_i
    c = 0
    strs[1].chars.map {|c| c.to_i}.each_with_index do |s, i|
        if i > c
            ans += i - c
            c = i
        end
        c += s
    end
    printf "Case #%d: %d\n", x + 1, ans
end