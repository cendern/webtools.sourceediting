(: Name: followingsibling-3 :)
(: Description: Evaluation of the following-sibling axis that is part of an "is" expression (return true). :)

(: insert-start :)
declare variable $input-context1 external;
(: insert-end :)

($input-context1/works/employee[12]/following-sibling::employee) is ($input-context1/works/employee[13])