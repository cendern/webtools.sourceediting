(:*******************************************************:)
(: Test: K-SeqInsertBeforeFunc-11                        :)
(: Written by: Frans Englich                             :)
(: Date: 2006-10-05T18:29:40+02:00                       :)
(: Purpose: A test whose essence is: `count(insert-before((1, 2, 3, 4), 30, ())) eq 4`. :)
(:*******************************************************:)
count(insert-before((1, 2, 3, 4), 30, ())) eq 4