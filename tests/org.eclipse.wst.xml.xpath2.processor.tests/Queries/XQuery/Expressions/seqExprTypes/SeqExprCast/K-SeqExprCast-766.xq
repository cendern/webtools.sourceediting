(:*******************************************************:)
(: Test: K-SeqExprCast-766                               :)
(: Written by: Frans Englich                             :)
(: Date: 2006-10-05T18:29:38+02:00                       :)
(: Purpose: Casting from xs:dayTimeDuration to xs:yearMonthDuration is allowed and should always succeed. :)
(:*******************************************************:)
xs:dayTimeDuration("P3DT2H") cast as xs:yearMonthDuration
                    ne
                  xs:yearMonthDuration("P1Y12M")