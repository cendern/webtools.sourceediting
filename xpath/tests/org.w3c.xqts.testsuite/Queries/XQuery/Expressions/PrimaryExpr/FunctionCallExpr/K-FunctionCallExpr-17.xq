(:*******************************************************:)
(: Test: K-FunctionCallExpr-17                           :)
(: Written by: Frans Englich                             :)
(: Date: 2006-10-05T18:29:36+02:00                       :)
(: Purpose: No function by name fn:format-time() exists(although one does in XSL-T). :)
(:*******************************************************:)
format-time(current-time(), "[H01]:[m01]", (), (), ())