// Create Jane Doe node
CREATE (jane:person {
  name: "Jane Doe",
  id: "dcbb9c06-625a-4b0e-af04-abda1c981c53"
})

// Create John Doe node
CREATE (john:person {
  name: "John Doe",
  id: "d1914989-2c92-4bc4-8cf7-eb59af586104"})

// Create Deb Janson node
CREATE (deb:person {
  name: "Deb Janson",
  id: "d1914989-2c92-4bc4-8cf7-eb59af586105"})


// Use WITH to pass the results to the next operation
WITH 1 as dummy

// Match the existing Jane and John nodes
MATCH (jane:person {name: "Jane Doe", id: "dcbb9c06-625a-4b0e-af04-abda1c981c53"})
MATCH (john:person {name: "John Doe", id: "d1914989-2c92-4bc4-8cf7-eb59af586104"})
MATCH (deb:person {name: "Deb Janson", id: "d1914989-2c92-4bc4-8cf7-eb59af586105"})

// Use WITH to pass the results to the next operation
WITH jane, john,deb

// Create HUSBAND_OF relationship
CREATE (john)-[:HUSBAND_OF]->(jane)
// Create WIFE_OF relationship
CREATE (jane)-[:WIFE_OF]->(john)
// Create WIFE_OF relationship
CREATE (deb)-[:SIBLING_OF]->(jane)

// Return the nodes and their new relationships
RETURN jane, john, deb