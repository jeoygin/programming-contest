#!/usr/bin/ruby

class Num
    attr_accessor :neg, :val

    def initialize(neg, val)
        @neg = neg
        @val = val
    end

    def to_i()
        if neg
            -val
        else
            val
        end
    end
end

def mul(num1, num2)
    neg = num1.neg ^ num2.neg
    if num1.val == 1
        Num.new(neg, num2.val)
    elsif num2.val == 1
        Num.new(neg, num1.val)
    elsif num1.val == num2.val
        Num.new(!neg, 1)
    else
        val = 9 - num1.val - num2.val
        neg ^= true if num1.val > num2.val
        neg ^= true if num1.val > val
        neg ^= true if num2.val > val
        Num.new(neg, val)
    end
end

t = gets.to_i
t.times do |c|
    params = gets.split(' ').map(&:to_i)
    l = params[0]
    x = params[1]
    nums = gets.strip.each_byte.map{|b| Num.new(false, b - 103)}
    forvals = Array.new(l+1)
    backvals = Array.new(l+1)
    forvals[0] = backvals[l] = Num.new(false, 1)
    l.times do |i|
        forvals[i+1] = mul(forvals[i], nums[i])
        backvals[l-1-i] = mul(nums[l-1-i], backvals[l-i])
    end
    repeat = Array.new()
    repeat[0] = forvals[0]
    repeat[1] = forvals[l]
    mark = {}
    orix = x
    len = x
    start = 0
    i = 0
    while i < x
        repeat[i+1] = mul(repeat[i], repeat[1])
        if mark[repeat[i+1].to_i].nil?
            mark[repeat[i+1].to_i] = i
        else
            x = i
            start = mark[repeat[i+1].to_i]
            len = i - start
            break
        end
        i += 1
    end
    ix = 0
    while ix < x
        il = 0
        while il < l
            break if mul(repeat[ix], forvals[il+1]).to_i == 2
            il += 1
        end
        break if il < l
        ix += 1
    end
    kx = 0
    while kx < x
        kl = l - 1
        while kl >= 0
            break if mul(backvals[kl], repeat[kx]).to_i == 4
            kl -= 1
        end
        break if kl >= 0
        kx += 1
    end
    ans = "NO"
    if ix < x && kx < x && (ix + kx) * l + il + 1 + l - kl < orix * l
        jx = orix - ix - kx - 1
        num = Num.new(false, 1)
        if jx == 0
            jl = il + 1
            while jl < kl
                num = mul(num, nums[jl])
                jl += 1
            end
        else
            jx -= 1
            jx = (jx - start) % len + start if jx > start
            num = mul(backvals[il + 1], repeat[jx])
            num = mul(num, forvals[kl])
        end
        ans = "YES" if num.to_i == 3
    end
    printf "Case #%d: %s\n", c + 1, ans
end

