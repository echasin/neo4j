import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Example from './example';
import ExampleDetail from './example-detail';
import ExampleUpdate from './example-update';
import ExampleDeleteDialog from './example-delete-dialog';

const ExampleRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Example />} />
    <Route path="new" element={<ExampleUpdate />} />
    <Route path=":id">
      <Route index element={<ExampleDetail />} />
      <Route path="edit" element={<ExampleUpdate />} />
      <Route path="delete" element={<ExampleDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ExampleRoutes;
