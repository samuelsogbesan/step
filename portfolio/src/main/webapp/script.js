// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

const add = (datapoint) => {
  const container = document.getElementById("data-container");
  const el = document.createElement('p');
  el.innerText = datapoint;
  container.appendChild(el);
}

const printJSONFromServer = () => {
  container = document.getElementById('data-container');
  fetch('/data')
  .then(data => data.json())
  .then(object => object.forEach(datapoint => add(datapoint)))
}

window.onload = () => document.getElementById('data-button').addEventListener('click', printJSONFromServer);