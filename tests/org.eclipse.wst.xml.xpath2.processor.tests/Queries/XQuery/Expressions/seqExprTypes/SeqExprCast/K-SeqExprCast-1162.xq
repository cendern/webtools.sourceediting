(:*******************************************************:)
(: Test: K-SeqExprCast-1162                              :)
(: Written by: Frans Englich                             :)
(: Date: 2006-10-05T18:29:39+02:00                       :)
(: Purpose: The xs:gMonth constructor function must be passed exactly one argument, not two. :)
(:*******************************************************:)
xs:gMonth(
      "--11"
    ,
                                                     
      "--11"
    )