(:*******************************************************:)
(: Test: K-QuantExprWithout-7                            :)
(: Written by: Frans Englich                             :)
(: Date: 2006-10-05T18:29:37+02:00                       :)
(: Purpose: some-quantification combined with fn:boolean(). :)
(:*******************************************************:)
some $i in subsequence((0, 1, 2, current-time()), 1, 3)
satisfies boolean($i treat as xs:integer)