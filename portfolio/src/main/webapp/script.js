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

const addCommentElement = (comment) => {
  const el = document.createElement("div");
  el.className = "comment";

  const nameContainer = document.createElement("p");
  nameContainer.className = "nickname";
  nameContainer.innerText = comment.nickname;
  el.appendChild(nameContainer);

  const commentContainer = document.createElement("p");
  commentContainer.className = "commentbox";
  commentContainer.innerText = comment.comment;
  el.appendChild(commentContainer);

  return el;
}

const json = (data) => [{nickname:"James",comment:"I like it here!"}];

const getComments = () => {
  fetch('/data')
  .then(data => json())
  .then(comments => {
    const container = document.getElementById('comments-container');
    comments.forEach(comment => container.appendChild(addCommentElement(comment)));
  });
}

const navigate = (pageId) => document.getElementById(pageId).scrollIntoView({behavior: 'smooth', block: 'start'});

const initialiseNavigation = () => {
  getComments();
  const navigationItems = document.getElementsByClassName('navigation-item');
  for (const item of navigationItems) {
    item.addEventListener('click', function() {
      navigate(this.getAttribute('data-link'));
    })
  }
}

window.onload = () => initialiseNavigation();
