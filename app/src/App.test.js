import { render, screen } from '@testing-library/react';
import App from './App';

test('renders main forecast table', () => {
  render(<App />);
  const tableElement = screen.getByLabelText(/main-forecast-table/i);
  expect(tableElement).toBeInTheDocument();
});

test('renders secondary forecast table', () => {
  render(<App />);
  const tableElement = screen.getByLabelText(/secondary-forecast-table/i);
  expect(tableElement).toBeInTheDocument();
});

test('renders unit selector', () => {
  render(<App />);
  const selectElement = screen.getByLabelText(/unit-select/i);
  expect(selectElement).toBeInTheDocument();
});

test('renders city input', () => {
  render(<App />);
  const inputElement = screen.getByLabelText(/city-input/i);
  expect(inputElement).toBeInTheDocument();
});

test('renders latitude input', () => {
  render(<App />);
  const inputElement = screen.getByLabelText(/latitude-input/i);
  expect(inputElement).toBeInTheDocument();
});

test('renders longitude input', () => {
  render(<App />);
  const inputElement = screen.getByLabelText(/longitude-input/i);
  expect(inputElement).toBeInTheDocument();
});