(:*******************************************************:)
(: Test: K-SeqExprCast-1316                              :)
(: Written by: Frans Englich                             :)
(: Date: 2006-10-05T18:29:39+02:00                       :)
(: Purpose: 'castable as' involving xs:base64Binary as sourceType and xs:NOTATION should fail due to it involving xs:NOTATION. :)
(:*******************************************************:)
not(xs:base64Binary("aaaa") castable as xs:NOTATION)