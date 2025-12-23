module.exports = {
    plugins: [require.resolve('@trivago/prettier-plugin-sort-imports')],
    trailingComma: 'none',
    tabWidth: 4,
    printWidth: 120,
    useTabs: false,
    semi: true,
    singleQuote: true,
    arrowParens: 'always',
    importOrder: ['^react', '<THIRD_PARTY_MODULES>', 'types', 'services', 'components', 'widgets', 'views', '.css'],
    importOrderSeparation: false,
    importOrderCaseInsensitive: true,
    importOrderSortSpecifiers: true,
    importOrderGroupNamespaceSpecifiers: true
};
