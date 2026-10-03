package com.google.javascript.jscomp;

public class TypeCheckBug810Test extends CompilerTestCase {

  @Override protected void setUp() throws Exception {
    super.setUp();
    enableTypeCheck(CheckLevel.WARNING); }

  public void testEnumUnionMissingProperty() {
    test(
        "/** @enum {string} / var E = {};" +
        "/* @type {E|{foo: string}} */ var x;" +
        "x.foo;",
        "element foo does not exist on this enum"); }

  public void testEnumUnionPropertyExistsOnEnum() {
    testSame(
        "/** @enum {string} / var E = {A: 'a'};" +
        "/* @type {E|Object} */ var x;" +
        "x.A;"); }

  public void testEnumUnionPropertyExistsInherited() {
    testSame(
        "/** @enum {string} / var E = {A: 'a'};" +
        "/* @type {E|Object} */ var x;" +
        "x.toString;"); }

  public void testEnumUnionMissingOnBothEnums() {
    test(
        "/** @enum {string} / var E = {};" +
        "/* @enum {string} / var F = {};" +
        "/* @type {E|F} */ var x;" +
        "x.foo;",
        "element foo does not exist on this enum"); }

  public void testInterfaceUnionMissingProperty() {
    test(
        "/** @interface / function I() {};" +
        "/* @type {I|{foo: string}} */ var x;" +
        "x.foo;",
        "property foo never defined on I"); }

  public void testNullUnionMissingProperty() {
    test(
        "/** @type {null|{foo: string}} */ var x;" +
        "x.foo;",
        "property foo never defined on null"); }

  public void testVoidUnionMissingProperty() {
    test(
        "/** @type {void|{foo: string}} */ var x;" +
        "x.foo;",
        "property foo never defined on void"); }

  public void testUnionAllMembersHaveProperty() {
    testSame(
        "/** @type {{foo: string}|{foo: number}} */ var x;" +
        "x.foo;"); }

  public void testEnumUnionElementExistsButPropertyMissingOnEnum() {
    test(
        "/** @enum {string} / var E = {A: 'a'};" +
        "/* @type {E|{bar: string}} */ var x;" +
        "x.bar;",
        "element bar does not exist on this enum"); }

  public void testEnumUnionRecordMissing() {
    test(
        "/** @enum {number} / var E = {A: 1};" +
        "/* @type {E|{x: number}} */ var x = null;" +
        "x.y;",
        "element y does not exist on this enum"); }

  public void testNestedUnionGetpropOnEnum() {
    test(
        "/** @enum {string} / var E = {};" +
        "/* @type {E|{a: {b: string}}} */ var x;" +
        "x.a.b;",
        "element a does not exist on this enum"); }

  public void testUnionMultipleEnumsMissingProperty() {
    test(
        "/** @enum {string} / var E = {};" +
        "/* @enum {string} / var F = {};" +
        "/* @type {E|F|{foo: string}} */ var x;" +
        "x.foo;",
        "element foo does not exist on this enum"); }
}
