// Define the start node name
WITH "John Doe" AS startNodeName

// Match all paths up to 2 hops from the start node
MATCH path = (start:person {name: startNodeName})-[*0..1]-(connected)
WHERE start <> connected OR start = connected

// Collect all unique nodes and relationships
WITH 
    COLLECT(DISTINCT start) + COLLECT(DISTINCT connected) AS allNodes,
    COLLECT(DISTINCT relationships(path)) AS allRels

// Unwind the collections to create a graph structure
UNWIND allNodes AS node
UNWIND allRels AS rels
UNWIND rels AS rel

// Return the graph structure as a D3.js-friendly JSON object
RETURN {
    nodes: COLLECT(DISTINCT {
        id: toString(id(node)),
        label: labels(node)[0],
        name: node.name,
        group: labels(node)[0]
    }),
    links: COLLECT(DISTINCT {
        source: toString(id(startNode(rel))),
        target: toString(id(endNode(rel))),
        type: type(rel)
    })
} AS d3Graph