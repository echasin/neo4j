WITH "John Doe" AS startNodeName
MATCH (start:person {name: startNodeName})-[*2]-(twoHopsAway)
WHERE start <> twoHopsAway
RETURN DISTINCT twoHopsAway.name AS NodeTwoHopsAway, labels(twoHopsAway) AS Labels