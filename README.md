# Front

Ceci correspond à la partie Android du projet.

## Comment coder proprement ?

- Cloner le repo sur votre machine. (`git clone`)
- Créer une nouvelle branche qui correspondra à votre feature (`git branch nomBranche`)
- Changer de branche (`git checkout nomBranche`)
- Vous êtes maintenant sur une branche nouvelle branche locale, le git ne la connaît pas !
- Vous pouvez ajouter du code qui sera push  (`git add files`), et laisser un message explicatif (`git commit -m MessageExpliquantVotreCommit`)
- Lors de votre premier push, votre a branche courante test n'aura pas de branche amont, il faudra la créer. (`git push --set-upstream origin nomBranche`)
- Les autres push se feront juste avec `git push`
- Certains éditeurs font déjà beaucoup de ces commmandes pour vous, mais c'est mieux de comprendre pour le jour où vous aure un problème.
- Lorsque votre feature sera fini, vous pourrez faire une pull request sur la branche **master**.

## Warning

- Lorsque vous devrez push, essayez de ne pas push les configs qui sont propres à chaque machine.
