{ pkgs, ... }:
{
  projectRootFile = "flake.nix";
  package = pkgs.treefmt;
  programs.ktlint.enable = true;
  programs.nixpkgs-fmt.enable = true;
  programs.biome.enable = true;
}
