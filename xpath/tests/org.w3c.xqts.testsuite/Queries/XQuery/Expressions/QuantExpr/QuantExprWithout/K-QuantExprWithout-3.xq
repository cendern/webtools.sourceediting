(:*******************************************************:)
(: Test: K-QuantExprWithout-3                            :)
(: Written by: Frans Englich                             :)
(: Date: 2006-10-05T18:29:37+02:00                       :)
(: Purpose: $foo has static type xs:integer; which cannot be compared to xs:string. :)
(:*******************************************************:)
some $foo in 1 satisfies $foo eq "1"